import pandas as pd
import csv
import requests
from io import BytesIO
from PIL import Image
import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing.image import img_to_array
from tensorflow.keras.models import load_model


def procesar_img(url,model,class_names):
    try:
        # Descargar la imagen desde la URL
        response = requests.get(url)
        response.raise_for_status()  # Verifica si hubo errores en la descarga
        #print(response)

        # Convertir la imagen a un objeto de Pillow
        img = Image.open(BytesIO(response.content)).convert('RGB')
        #print(img)
        # Redimensionar si es necesario (por ejemplo, 224x224)
        img = img.resize((64, 64))

        # Convertir la imagen a un arreglo y normalizarla
        img_array = img_to_array(img) / 255.0
        img_array = np.expand_dims(img_array, axis=0)  # Añadir la dimensión del batch
        #print(img_array)
        predictions = model.predict(img_array)
        predicted_class_index = np.argmax(predictions, axis=1)[0]
        predicted_class_name = class_names[predicted_class_index]
    
        # Mostrar el resultado
        #print(f"Predicted class: {predicted_class_name}")
        return predicted_class_name
    
    except Exception as e:
        #print(f"Error al descargar o procesar la imagen desde {url}: {e}")
        return None

def procesar_csv(ruta_csv,model, class_names):
    # Leer el archivo CSV
    df = pd.read_csv(ruta_csv)
    with open('database.csv', mode='w', newline='') as archivo_csv:
        # Iterar sobre las filas del DataFrame
        for index, row in df.iterrows():
            print(f"Procesando SKU: {row['SKU']}")
            resultados=[]

            resultados.append(row['SKU'])
            resultados.append(row['Name'])
            resultados.append(row['Atributos'])

            # Iterar sobre las columnas de imágenes (Img 1, Img 2, ..., Img 29)
            for col in [col for col in df.columns if col.startswith('Imagen')]:
                url = row[col]
                if pd.notna(url):  # Verificar que no sea NaN
                    class_image = procesar_img(url, model, class_names)
                    resultados.append(class_image)
            
            
                escritor_csv = csv.writer(archivo_csv)
                escritor_csv.writerow(resultados)
    
def leer_clases_de_txt(ruta_txt='clases.txt'):
    try:
        with open(ruta_txt, 'r') as f:
            class_names = [line.strip() for line in f.readlines()]
        #print(f"Clases leídas desde {ruta_txt}: {class_names}")
        return class_names
    except Exception as e:
        print(f"Error al leer el archivo: {e}")
        return []
    
# Uso
model = tf.keras.models.load_model("modelo/modelo.keras")
class_names= leer_clases_de_txt()

ruta_csv = 'imgs.csv'
procesar_csv(ruta_csv,model, class_names)
