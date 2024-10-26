#!/usr/bin/env python
# coding: utf-8

# In[4]:


# Instalar las bibliotecas necesarias
get_ipython().system('pip install google-cloud-aiplatform')
get_ipython().system('pip install google-cloud-storage')
get_ipython().system('pip install numpy')
get_ipython().system('pip install tensorflow')

# Importar las bibliotecas
from google.cloud import aiplatform
from google.cloud import storage
import numpy as np
import tensorflow as tf
from tensorflow.keras.applications import ResNet50
from tensorflow.keras.preprocessing import image
from tensorflow.keras.applications.resnet50 import preprocess_input
import os

# Inicializar Vertex AI y Google Cloud Storage
aiplatform.init(
    project='pristine-skein-439823-i5',
    location='us-central1',
)

storage_client = storage.Client()
bucket_name = 'tu-bucket'
bucket = storage_client.bucket(bucket_name)

# Modelo para generar embeddings
model = ResNet50(weights='imagenet', include_top=False, pooling='avg')

def get_embedding(img_path):
    img = image.load_img(img_path, target_size=(224, 224))
    img_data = image.img_to_array(img)
    img_data = np.expand_dims(img_data, axis=0)
    img_data = preprocess_input(img_data)
    embedding = model.predict(img_data)
    return embedding.flatten()

def save_embeddings_to_gcs(embeddings, file_path):
    blob = bucket.blob(file_path)
    blob.upload_from_string(np.save(file_path, embeddings))

def load_embeddings_from_gcs(file_path):
    blob = bucket.blob(file_path)
    blob.download_to_filename(file_path)
    return np.load(file_path)

def remove_duplicates(embeddings, threshold=0.9):
    unique_embeddings = []
    for emb in embeddings:
        if all(np.linalg.norm(emb - unique_emb) > threshold for unique_emb in unique_embeddings):
            unique_embeddings.append(emb)
    return np.array(unique_embeddings)

# Crear o cargar embeddings y eliminar duplicados
embeddings_file = 'embeddings.npy'
image_paths = ['ruta/a/imagen1.jpg', 'ruta/a/imagen2.jpg', ...]  # Lista de rutas a las imágenes

if not bucket.blob(embeddings_file).exists():
    embeddings = np.array([get_embedding(img_path) for img_path in image_paths])
    embeddings = remove_duplicates(embeddings)
    save_embeddings_to_gcs(embeddings, embeddings_file)
else:
    embeddings = load_embeddings_from_gcs(embeddings_file)

# Función principal para comparar una nueva imagen
def find_most_similar(new_image_path, embeddings, threshold=0.8):
    new_embedding = get_embedding(new_image_path)
    similarities = np.dot(embeddings, new_embedding) / (np.linalg.norm(embeddings, axis=1) * np.linalg.norm(new_embedding))
    max_similarity = np.max(similarities)
    if max_similarity > threshold:
        similar_indices = np.where(similarities > threshold)[0]
        return similar_indices, max_similarity
    return None, max_similarity

new_image_path = 'ruta/a/nueva_imagen.jpg'
similar_indices, similarity = find_most_similar(new_image_path, embeddings)

if similar_indices is not None:
    print(f'La nueva imagen es similar a las imágenes en los índices: {similar_indices} con una similitud de {similarity}')
else:
    print(f'No se encontraron imágenes similares con una similitud mayor al umbral.')


# In[ ]:




