# Build stage
FROM node:18 as builder
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci --quiet --legacy-peer-deps

# Copy source code
COPY . .

# Build the application
RUN npm run build --configuration=production

# Create output directory if it doesn't exist
RUN mkdir -p dist/my-frontend

# Production stage
FROM nginx:stable

# Copy build output
COPY --from=builder /app/dist/my-frontend /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Install wget for healthcheck
RUN apt-get update && apt-get install -y wget

# Expose port
EXPOSE 80

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --spider -q http://localhost/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]

