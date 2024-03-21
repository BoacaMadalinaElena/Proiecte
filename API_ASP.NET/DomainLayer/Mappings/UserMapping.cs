using DomainLayer.Models;
using Microsoft.EntityFrameworkCore;

namespace DomainLayer.Mappings
{
    public static class UserMapping
    {
        public static void Map(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Models.User>()
                .Property(c => c.Name)
                .HasColumnType("text")
                .HasMaxLength(50)
                .IsRequired();

            modelBuilder.Entity<User>()
                .Property(c => c.Email)
                .HasColumnType("text")
                .HasMaxLength(100)
                .IsRequired();

            modelBuilder.Entity<User>()
                .Property(c => c.Password)
                .HasColumnType("text")
                .HasMaxLength(50)
                .IsRequired();

            modelBuilder.Entity<User>()
                  .Property(c => c.IsCreator)
                  .HasColumnType("bit")
                  .IsRequired();

            modelBuilder.Entity<User>()
                  .Property(c => c.Description)
                  .HasColumnType("text")
                  .HasMaxLength(200)
                  .IsRequired(false);

            modelBuilder.Entity<User>()
                 .Property(c => c.Img)
                 .HasColumnType("varbinary(max)")
                 .IsRequired(false);

            modelBuilder.Entity<User>()
                  .HasMany(e => e.Favorites);
        }
    }
}
