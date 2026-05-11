# Investi MySQL Database - Docker Setup

This Docker setup replicates your XAMPP MySQL environment for deployment on a VPS.

## 📋 What's Included

- **MySQL 8.0** database server
- **Database**: `3a8`
- **Root Password**: `root` (matches your XAMPP setup)
- **Additional User**: `investi` / `investi123`
- **Port**: 3306 (exposed)
- **Persistent Storage**: Data is saved in a Docker volume

## 🚀 Quick Start

### 1. Build and Start the Database

```bash
cd investi-_java
docker-compose up -d
```

### 2. Check if it's Running

```bash
docker-compose ps
```

You should see `investi-mysql` with status "Up"

### 3. View Logs

```bash
docker-compose logs -f mysql
```

### 4. Connect to MySQL

From your host machine:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p
# Password: root
```

Or using the investi user:
```bash
mysql -h 127.0.0.1 -P 3306 -u investi -p
# Password: investi123
```

## 🔧 Management Commands

### Stop the Database
```bash
docker-compose stop
```

### Start the Database
```bash
docker-compose start
```

### Restart the Database
```bash
docker-compose restart
```

### Stop and Remove Everything (including data)
```bash
docker-compose down -v
```

### Stop but Keep Data
```bash
docker-compose down
```

## 📦 Deploying to VPS

### 1. Copy Files to VPS

```bash
# From your local machine
scp -i investikey.pem Dockerfile docker-compose.yml 3a8.sql ec2-user@54.90.222.165:~/investi-db/
```

### 2. SSH into VPS

```bash
ssh -i investikey.pem ec2-user@54.90.222.165
```

### 3. Install Docker on VPS (Amazon Linux 2023)

```bash
# Update system
sudo yum update -y

# Install Docker
sudo yum install -y docker

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Add user to docker group
sudo usermod -a -G docker ec2-user

# Log out and back in for group changes to take effect
exit
```

Then SSH back in and continue:

```bash
# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version
```

### 4. Start Database on VPS

```bash
cd ~/investi-db
docker-compose up -d
```

### 5. Configure Security Group (AWS)

Make sure port 3306 is open in your EC2 security group if you want to connect remotely.

**⚠️ Security Warning**: Only open port 3306 to trusted IP addresses, not to 0.0.0.0/0

## 🔐 Connecting Your Java App

Update your `config.properties`:

### For Local Development (Docker on localhost)
```properties
db.url=jdbc:mysql://localhost:3306/3a8
db.user=root
db.password=root
```

### For VPS Deployment
```properties
db.url=jdbc:mysql://54.90.222.165:3306/3a8
db.user=investi
db.password=investi123
```

Or use localhost if your Java app is also on the VPS:
```properties
db.url=jdbc:mysql://localhost:3306/3a8
db.user=investi
db.password=investi123
```

## 🗄️ Database Backup

### Export Database
```bash
docker exec investi-mysql mysqldump -u root -proot 3a8 > backup_$(date +%Y%m%d).sql
```

### Import Database
```bash
docker exec -i investi-mysql mysql -u root -proot 3a8 < backup.sql
```

## 🐛 Troubleshooting

### Container won't start
```bash
docker-compose logs mysql
```

### Reset everything
```bash
docker-compose down -v
docker-compose up -d
```

### Check MySQL is accepting connections
```bash
docker exec investi-mysql mysql -u root -proot -e "SELECT 1"
```

## 📊 Database Info

- **Database Name**: 3a8
- **Tables**: users, evenement, inscription, project, investment, collaboration, product, sale, course, badges, forum_posts, and more
- **Character Set**: utf8mb4
- **Collation**: utf8mb4_unicode_ci
- **Engine**: InnoDB

## 🔄 Updating the Database Schema

If you need to update the schema:

1. Update `3a8.sql` file
2. Rebuild the container:
```bash
docker-compose down -v
docker-compose up -d --build
```

**Note**: This will delete all data! For production, use migrations instead.
