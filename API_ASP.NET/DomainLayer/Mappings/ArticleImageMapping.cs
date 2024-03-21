using Microsoft.EntityFrameworkCore;

namespace DomainLayer.Mappings
{
    public class ArticleImageMapping
    {
        public static void Map(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Models.ArticleImage>()
                .Property(c => c.Content)
                .HasColumnType("varchar(max)")
                .HasMaxLength(50)
                .IsRequired();
        }
    }
}
