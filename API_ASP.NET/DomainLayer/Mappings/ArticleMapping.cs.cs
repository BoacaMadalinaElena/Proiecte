using DomainLayer.Models;
using Microsoft.EntityFrameworkCore;

namespace DomainLayer.Mappings
{
    public static class ArticleMapping
    {
        public static void Map(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Models.Article>()
                .Property(c => c.Title)
                .HasColumnType("text")
                .HasMaxLength(50)
                .IsRequired();

            modelBuilder.Entity<Article>()
                .Property(c => c.Content)
                .HasColumnType("text")
                .IsRequired();

            modelBuilder.Entity<Article>()
                .Property(c => c.City)
                .HasColumnType("text")
                .HasMaxLength(50)
                .IsRequired();

            modelBuilder.Entity<Article>()
                .Property(c => c.Facebook)
                .HasColumnType("text")
                .HasMaxLength(100);

            modelBuilder.Entity<Article>()
                .Property(c => c.Twitter)
                .HasColumnType("text")
                .HasMaxLength(100);

            modelBuilder.Entity<Article>()
                .Property(c => c.Instagram)
                .HasColumnType("text")
                .HasMaxLength(100);
        }
    }
}
