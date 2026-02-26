# Deployment commands 


## Auth
``` bash
gcloud auth login
gcloud config set `project [PROJECT_ID]
```

## Enable API
``` bash
gcloud services enable run.googleapis.com \
                       artifactregistry.googleapis.com \
                       cloudbuild.googleapis.com \
                       secretmanager.googleapis.com
```

## Create a Artifact Registry repository
``` bash
gcloud artifacts repositories create tcommerce-repo \
    --repository-format=docker \
    --location=southamerica-west1 \
    --description="Docker repository for TCommerce"
```

## Build and push the image
``` bash
gcloud builds submit --tag southamerica-west1-docker.pkg.dev/[PROJECT_ID]/tcommerce-repo/tcommerce-api:v1 .
```

## Bucket configuration

### Create Service account
``` bash
gcloud iam service-accounts create cloud-run-tcommerce
```
Give authorization to the service account
``` bash
gcloud storage buckets add-iam-policy-binding gs://[YOUR_BUCKET_NAME] \
    --member="serviceAccount:cloud-run-tcommerce@[PROJECT_ID].iam.gserviceaccount.com" \
    --role="roles/storage.objectAdmin"
```

## DEPLOY CONFIGURATION

Set secrets variables in GCP
``` bash
echo -n "tu-api-key" | gcloud secrets create RESEND_API_KEY --data-file=-
```

``` bash
gcloud run deploy tcommerce-api \
    --image southamerica-west1-docker.pkg.dev/tesis-experiment/tcommerce-repo/tcommerce-api:v1 \
    --service-account cloud-run-tcommerce@tesis-experiment.iam.gserviceaccount.com \
    --region southamerica-west1 \
    --allow-unauthenticated \
    --set-env-vars="GCS_BUCKET_NAME=t-commerce_storage,EMAIL_REST_RESEND_ENABLED=true" \
    --set-secrets="JWT_SECRET_KEY=JWT_SECRET:latest,EMAIL_REST_RESEND_API_KEY=RESEND_API_KEY:latest"
```