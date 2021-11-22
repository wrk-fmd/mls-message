FROM python:3-alpine
WORKDIR /app

# Install dependencies
RUN pip install --no-cache-dir pyserial pyyaml pika

# Run as non-root
RUN adduser --gecos "" --shell "/sbin/nologin" --disabled-password --no-create-home python

# Copy all source files
COPY ./src/*.py ./
RUN chown python:python *.py

# Run the application
USER python
ENTRYPOINT ["python", "main.py", "/config/application-docker.yml"]
