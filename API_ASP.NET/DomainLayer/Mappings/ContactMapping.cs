using DomainLayer.Models;
using Microsoft.EntityFrameworkCore;

namespace DomainLayer.Mappings
{
    public static class ContactMapping
    {
        public static void Map(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Models.Contact>()
                .Property(c => c.Name)
                .HasColumnType("text")
                .HasMaxLength(50);

            modelBuilder.Entity<Models.Contact>()
                .Property(c => c.Subject)
                .HasColumnType("text")
                .HasMaxLength(100)
                .IsRequired();

            modelBuilder.Entity<Models.Contact>()
                .Property(c => c.Message)
                .HasColumnType("text")
                .HasMaxLength(300)
                .IsRequired();

            modelBuilder.Entity<Models.Contact>()
                .Property(c => c.Email)
                .HasColumnType("text")
                .HasMaxLength(50)
                .IsRequired();
        }
    }
}
