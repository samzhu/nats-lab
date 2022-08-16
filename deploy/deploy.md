

## Install and Use Docker on Ubuntu 22.04

### Docker Engine
``` bash
sudo apt update -y
sudo apt install apt-transport-https ca-certificates curl software-properties-common -y
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update -y
apt-cache policy docker-ce
sudo apt install docker-ce -y
sudo systemctl enable docker
sudo systemctl status docker
```

### Executing the Docker Command Without Sudo
``` bash
sudo usermod -aG docker ${USER}
```

### Docker Compose
``` bash
mkdir -p ~/.docker/cli-plugins/
curl -SL https://github.com/docker/compose/releases/download/v2.3.3/docker-compose-linux-x86_64 -o ~/.docker/cli-plugins/docker-compose
chmod +x ~/.docker/cli-plugins/docker-compose
docker compose version
```

sudo apt update && sudo apt upgrade

https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-22-04
https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-compose-on-ubuntu-22-04

## server1


``` bash
mkdir demo && cd demo
mkdir init-mongodb
```

``` bash
cat << 'EOF' > init-mongodb/mongo-init.js
db.auth("user1", "pw123456");

db = db.getSiblingDB("devdoc");

db.createUser({
  user: "user1",
  pwd: "pw123456",
  roles: [
    {
      role: "readWrite",
      db: "devdoc",
    },
  ],
});

db.createCollection('account');
EOF

cat << 'EOF' > docker-compose.yml
version: "3.3"

services:

  app:
    container_name: app
    image: ghcr.io/samzhu/nats-lab/account-test:10
    restart: always
    environment:
      spring.profiles.active : 'compose'
    ports:
      - "8080:8080"
    networks:
      - privateBridge

  mongo:
    container_name: mongo
    image: 'mongo:4.4.15'
    restart: always
    ports:
      - '27017:27017'
    environment:
      MONGO_INITDB_ROOT_USERNAME: 'user1'
      MONGO_INITDB_ROOT_PASSWORD: 'pw123456'
      MONGO_INITDB_DATABASE: 'devdoc'
    volumes:
      - ./init-mongodb:/docker-entrypoint-initdb.d
    networks:
      - privateBridge

  nats:
    container_name: nats
    image: "nats:2.8.4"
    restart: always
    command:
      # - "--debug"
      - "--http_port"
      - "8222"
      - "--port"
      - "4222"
      - "--jetstream"
      - "--cluster_name"
      - "c1"
      - "--cluster" 
      - "nats://0.0.0.0:6222"
      - "--routes" 
      - "nats://${ROUTE_DOMAIN}:6222"
      - "--name"
      - "${SERVER_NAME}"
    ports:
      - "4222:4222"
      - "8222:8222"
      - "6222:6222"
    networks:
      - privateBridge

networks:
  privateBridge:
    driver: bridge
EOF

cat << 'EOF' > .env
SERVER_NAME=c1-n1
ROUTE_DOMAIN=10.140.0.50
EOF
```

``` bash
cat << 'EOF' > .env
SERVER_NAME=c1-n2
ROUTE_DOMAIN=10.140.0.50
EOF
```

``` bash
docker compose convert
docker compose up -d nats
docker compose up -d mongo
docker compose logs -f
docker compose up -d app
```

sudo apt install nmap -y
nmap 104.199.213.248 -p 6222