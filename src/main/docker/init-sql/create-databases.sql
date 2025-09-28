IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'CarRentalDb')
BEGIN
    CREATE DATABASE [CarRentalDb];
END
GO
