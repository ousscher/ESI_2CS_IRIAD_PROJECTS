from mpi4py import MPI
import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.cluster import KMeans
from sklearn.decomposition import PCA
import matplotlib.pyplot as plt
import time
import warnings
warnings.filterwarnings('ignore')

def master(comm, size):
    start_time = time.time()
    print("[Master] Démarrage du processus principal")

    # 1. Chargement et prétraitement des données
    print("[Master] Chargement des données...")
    df = pd.read_csv('../data/retail_customer_dataset.csv')
    
    features = ['age', 'revenu_annuel', 'montant_moyen_achat', 'frequence_achats_mensuelle',
                'temps_site_minutes', 'taux_ouverture_emails', 'pct_tech', 'pct_vetements',
                'pct_alimentation', 'pct_maison', 'taux_retour', 'anciennete_jours',
                'nb_contacts_support', 'jours_depuis_dernier_achat', 'pct_achats_promotion',
                'valeur_client_lifetime', 'taux_abandon_panier', 'heures_achat', 'distance_magasin_km']
    
    cat_features = ['canal_acquisition', 'appareil_prefere', 'region', 'genre']
    
    print("[Master] Prétraitement des données...")
    numeric_transformer = StandardScaler()
    categorical_transformer = OneHotEncoder(drop='first', sparse_output=False)
    preprocessor = ColumnTransformer(
        transformers=[
            ('num', numeric_transformer, features),
            ('cat', categorical_transformer, cat_features)
        ])
    X_processed = preprocessor.fit_transform(df[features + cat_features])
    
    n_clusters = 6
    r_iterations = 10 
    random_state = 42
    candidate_fraction = 0.1
    
    # 2. KMeans initial (pour r itérations)
    print(f"[Master] Exécution de KMeans pour {r_iterations} itérations...")
    km1 = KMeans(
        n_clusters=n_clusters,
        init='k-means++',
        max_iter=r_iterations,
        n_init=1,
        tol=1e-4,
        random_state=random_state
    )
    labels = km1.fit_predict(X_processed)
    centroids = km1.cluster_centers_.copy()
    
    # 3. Diviser le travail de calcul des médoïdes par cluster
    data_splits = []
    for j in range(n_clusters):
        cluster_idx = np.where(labels == j)[0]
        if len(cluster_idx) > 0:
            data_splits.append({
                'cluster_id': j,
                'points': X_processed[cluster_idx],
                'indices': cluster_idx,
                'centroid': centroids[j],
                'candidate_fraction': candidate_fraction,
                'random_state': random_state
            })
    
    worker_count = size - 1
    work_per_worker = [[] for _ in range(worker_count)]
    
    for i, split in enumerate(data_splits):
        worker_idx = i % worker_count
        work_per_worker[worker_idx].append(split)
    
    # 4. Envoyer le travail à chaque worker
    for rank in range(1, size):
        work = work_per_worker[rank-1]
        if work:
            comm.send(work, dest=rank, tag=11)
            print(f"[Master] Envoi de {len(work)} clusters au worker {rank}")
        else:
            comm.send([], dest=rank, tag=11)
            print(f"[Master] Aucun cluster à envoyer au worker {rank}")
    
    # 5. Collecter les médoïdes optimaux
    new_centroids = centroids.copy()
    for rank in range(1, size):
        medoid_results = comm.recv(source=rank, tag=22)
        print(f"[Master] Reçu {len(medoid_results)} médoïdes du worker {rank}")
        
        for result in medoid_results:
            cluster_id = result['cluster_id']
            medoid = result['medoid']
            improved = result['improved']
            
            if improved:
                new_centroids[cluster_id] = medoid
                print(f"[Master] Amélioré cluster {cluster_id} avec un médoïde")
    
    # 6. KMeans final jusqu'à convergence
    print("[Master] Exécution du KMeans final avec les médoïdes...")
    km_final = KMeans(
        n_clusters=n_clusters,
        init=new_centroids,
        n_init=1,
        max_iter=300,
        tol=1e-4,
        random_state=random_state
    )
    final_labels = km_final.fit_predict(X_processed)
    end_time = time.time() - start_time
    # 7. Visualisation avec PCA
    print("[Master] Création de la visualisation PCA...")
    pca = PCA(n_components=2)
    X_pca = pca.fit_transform(X_processed)
    
    plt.figure(figsize=(10, 8))
    plt.scatter(X_pca[:, 0], X_pca[:, 1], c=final_labels, cmap='viridis', alpha=0.5)
    
    centroids_pca = pca.transform(km_final.cluster_centers_)
    plt.scatter(centroids_pca[:, 0], centroids_pca[:, 1], s=200, marker='X', c='red', label='Centroids')
    
    plt.title('Clusters visualisés avec PCA')
    plt.xlabel('Composante Principale 1')
    plt.ylabel('Composante Principale 2')
    plt.legend()
    plt.savefig('clustering_result.png')
    print(f"[Master] Temps d'exécution total: {end_time:.2f} secondes")
    plt.show()

def worker(comm, rank):
    # 1. Recevoir les données et les tâches
    print(f"[Worker {rank}] En attente des données...")
    work = comm.recv(source=0, tag=11)
    
    if not work:
        print(f"[Worker {rank}] Aucun travail reçu, terminaison...")
        comm.send([], dest=0, tag=22)
        return
    
    print(f"[Worker {rank}] Reçu {len(work)} clusters pour calculer les médoïdes")
    
    # 2. Calculer les médoïdes optimaux pour chaque cluster
    medoid_results = []
    
    for cluster_data in work:
        cluster_id = cluster_data['cluster_id']
        points = cluster_data['points']
        indices = cluster_data['indices']
        centroid = cluster_data['centroid']
        candidate_fraction = cluster_data['candidate_fraction']
        random_state = cluster_data['random_state']
        
        print(f"[Worker {rank}] Traitement du cluster {cluster_id} avec {len(points)} points")
        
        # 2.1 Calculer le coût actuel (somme des distances au centroïde)
        cost_current = np.sum(np.linalg.norm(points - centroid, axis=1))
        
        # 2.2 Échantillonner une fraction de points comme candidats
        rng = np.random.RandomState(random_state)
        m = max(1, int(candidate_fraction * len(points)))
        cand_indices = rng.choice(range(len(points)), size=m, replace=False)
        candidates = points[cand_indices]
        
        # 2.3 Calculer les coûts pour chaque candidat
        best_medoid = centroid
        improved = False
        
        # Calculer la matrice de distances entre chaque point et chaque candidat
        # (n_points, m_candidates)
        start_time = time.time()
        dists = np.linalg.norm(points[:, None] - candidates[None, :, :], axis=2)
        costs = dists.sum(axis=0)
        
        best_idx = np.argmin(costs)
        best_cost = costs[best_idx]
        
        if best_cost < cost_current:
            best_medoid = candidates[best_idx]
            improved = True
            print(f"[Worker {rank}] Amélioré cluster {cluster_id}: {cost_current:.2f} -> {best_cost:.2f}")
        
        medoid_results.append({
            'cluster_id': cluster_id,
            'medoid': best_medoid,
            'improved': improved
        })
        
        print(f"[Worker {rank}] Cluster {cluster_id} traité en {time.time() - start_time:.2f} secondes")
    
    # 3. Envoyer les résultats au master
    comm.send(medoid_results, dest=0, tag=22)
    print(f"[Worker {rank}] Tous les médoïdes calculés et envoyés au master")

def main():
    comm = MPI.COMM_WORLD
    rank = comm.Get_rank()
    size = comm.Get_size()

    if size < 2:
        if rank == 0:
            print("⚠️ Lancez avec au moins 2 processus (1 master + workers)")
        return

    if rank == 0:
        master(comm, size)
    else:
        worker(comm, rank)

if __name__ == "__main__":
    main()