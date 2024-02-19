## 사용할 베이스 이미지 선택
#FROM python:3.8
#
## 작업 디렉토리 설정
#WORKDIR /app
#
## 필요한 Python 패키지 설치
#COPY requirements.txt /app/
#RUN pip install --no-cache-dir -r requirements.txt
#
## 애플리케이션 코드 복사
#COPY . /app
#
## 서버 실행
##CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
## Define environment variable
##ENV UVICORN_CMD="uvicorn predict_content:app --host 0.0.0.0 --port 8000 --reload"
#
## Run the application when the container launches
##CMD ["sh", "-c", "$UVICORN_CMD"]
#CMD ["python", "predict_content.py"]

# Python 이미지를 베이스로 사용
FROM python:3.11.6

# 작업 디렉토리 설정
WORKDIR /app

# 가상 환경 생성
#RUN python -m venv /app/venv

# 가상 환경 활성화
#ENV PATH="/app/venv/bin:$PATH"

# 의존성 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 코드 복사
COPY .. /app

# 애플리케이션 실행
CMD ["python", "predict_content.py"]
