from fastapi import FastAPI, HTTPException, Query
from urllib.parse import unquote
import torch
from transformers import BertTokenizer
from custom_model import CustomModel
import argparse
import re
import os
from pydantic import BaseModel


app = FastAPI()

def str2bool(s):
    if s.lower() == 'true':
        return True
    elif s.lower() == 'false':
        return False
    else:
        raise ValueError('Invalid boolean value')

def clean_text(text):
    text = re.sub(r'[0-9]\.', '' , text)
    text = re.sub(r'\([0-9]\)', '', text)
    text = re.sub(r'[\n:*-/→\"\']', '', text)
    text = re.sub(r'[ㄱ-ㅎ]', ' ', text)
    text = re.sub(r'[\s]+', ' ', text)
    return text.strip()

def tokenize(tokenizer, text):
    return torch.tensor(tokenizer(text, padding='max_length', truncation=True, max_length=100)['input_ids']).unsqueeze(0)

def logits2labels(logits):
    labels1 = torch.argmax(logits[0], dim=1).tolist()
    labels2 = torch.argmax(logits[1], dim=1).tolist()
    labels3 = torch.argmax(logits[2], dim=1).tolist()
    return labels1, labels2, labels3

def predict_content(text):
    text = clean_text(text)
    tokenizer = BertTokenizer.from_pretrained('klue/bert-base')
    tokenized = tokenize(tokenizer, text)

    # model = torch.load(model_path, map_location=torch.device('cpu'))  # GPU not used
    model = torch.jit.load('traced_model.pt')
    # model = torch.load(model_path)  # Use this line if GPU is available

    # print(model)
    # print(tokenized)
    logits = model(tokenized)
    labels1, labels2, labels3 = logits2labels(logits)

    cur_labels = []
    if labels1[0] == 1:
        cur_labels.append('1')
    if labels2[0] == 1:
        cur_labels.append('2')
    if labels3[0] == 1:
        cur_labels.append('3')

    if cur_labels != []:
        return ','.join(cur_labels)
    else:
        return '0'

# Define a route for handling predictions
# get data from parameter
@app.post('/predict')
def predict(text: str = Query(...)):
    try:
        # Get the text data from the URL parameter
        text = unquote(text)

        # Use your existing prediction code
        result = predict_content(text)

        # Return the result as JSON
        return {"result": result}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
#
# class Item(BaseModel):
#     text:str
#
# # get data from request body
# @app.post('/predict')
# async def predict(item: Item):
#    try:
#        result = predict_content(item.text)
#        return {"result": result}
#    except Exception as e:
#        raise HTTPException(status_code=500, detail=str(e))


# Run the FastAPI app
if __name__ == '__main__':
    print(os.getcwd())
    print('kmong')
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
