#!/bin/bash

# Xác định branch hiện tại của Git
# BRANCH=$(git rev-parse --abbrev-ref HEAD)

# echo "Deploying with branch: $BRANCH"

# # Di chuyển đến thư mục chứa mã nguồn microservice eventsourcing
# cd app/LV-BackEnd/

# # Cập nhật mã nguồn từ Git
# git fetch -a
# git checkout $BRANCH
# git pull

# Liệt kê các dịch vụ cần kéo image từ Docker Hub
services="apigateway chatservice discoveryserver mediaservice notificationservice orderservice productservice searchservice userservice"

# Pull tất cả các image từ Docker Hub (các service đã được build)
for service in $services; do
  echo "🔄 Pulling Docker image for $service..."
  docker-compose -f docker-compose.yml pull $service
done

# Khởi động tất cả các dịch vụ với Docker Compose
docker-compose -f docker-compose.yml up -d &

# Chờ một thời gian để DB khởi động
sleep 10  # Điều chỉnh thời gian nếu cần

# Triển khai các service lên Kubernetes
for service in $services; do
  echo "🚀 Deploying $service to Kubernetes..."
  kubectl apply -f ./$service/deployment.yaml
  kubectl apply -f ./$service/hpa.yaml
done

# Kiểm tra trạng thái deployment
for service in $services; do
  echo "🔍 Verifying $service deployment..."
  kubectl rollout status deployment/$service
done

# Xóa image không sử dụng để giải phóng dung lượng
echo "🧹 Cleaning up unused Docker images..."
docker system prune -af