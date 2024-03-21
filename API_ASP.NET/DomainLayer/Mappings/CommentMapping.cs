using Microsoft.EntityFrameworkCore;

namespace DomainLayer.Mappings
{
    public static class CommentMapping
    {
        public static void Map(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Models.Comment>()
                .Property(c => c.Content)
                .HasColumnType("text")
                .HasMaxLength(500)
                .IsRequired();
        }
    }
}
