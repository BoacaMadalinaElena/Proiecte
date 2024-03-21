using Microsoft.EntityFrameworkCore;

namespace DomainLayer.Mappings
{
    public static class CategoryMapping
    {
        public static void Map(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Models.Category>()
                .Property(c => c.Name)
                .HasColumnType("text")
                .HasMaxLength(50)
                .IsRequired();
        }
    }
}
