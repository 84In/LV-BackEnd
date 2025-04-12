# This script builds a Docker image for the user service and saves it to a tar file.
docker build -t userservice:local -f userservice/Dockerfile .
docker build -t productservice:local -f productservice/Dockerfile .
docker build -t orderservice:local -f orderservice/Dockerfile .
docker build -t mediaservice:local -f mediaservice/Dockerfile .
docker build -t notificationservice:local -f notificationservice/Dockerfile .
docker build -t authservice:local -f authservice/Dockerfile .
docker build -t searchservice:local -f searchservice/Dockerfile .

# It also builds a Docker image for the user service with a specific tag and saves it to a tar file.
docker save -o userservice.tar userservice:local
docker save -o productservice.tar productservice:local
docker save -o orderservice.tar orderservice:local
docker save -o mediaservice.tar mediaservice:local
docker save -o notificationservice.tar notificationservice:local
docker save -o authservice.tar authservice:local
docker save -o searchservice.tar searchservice:local
# The script then loads the Docker images into the k3s container runtime.
echo "Loading images into k3s container runtime..."
sudo k3s ctr images import userservice.tar
sudo k3s ctr images import productservice.tar
sudo k3s ctr images import orderservice.tar
sudo k3s ctr images import mediaservice.tar
sudo k3s ctr images import notificationservice.tar
sudo k3s ctr images import authservice.tar
sudo k3s ctr images import searchservice.tar    
echo "Images loaded into k3s container runtime."

#x Finally, it cleans up the tar files. 
echo "start deploying services..."
kubectl apply -f userservice/deployment.yaml
kubectl apply -f productservice/deployment.yaml
kubectl apply -f orderservice/deployment.yaml
kubectl apply -f mediaservice/deployment.yaml
kubectl apply -f notificationservice/deployment.yaml
kubectl apply -f authservice/deployment.yaml
kubectl apply -f searchservice/deployment.yaml
echo "All services deployed successfully."

# The script also applies Kubernetes deployment configurations for each service.
# The script then cleans up the tar files to free up space. 
rm userservice.tar
rm productservice.tar
rm orderservice.tar
rm mediaservice.tar
rm notificationservice.tar
rm authservice.tar
rm searchservice.tar
echo "All tar files removed."