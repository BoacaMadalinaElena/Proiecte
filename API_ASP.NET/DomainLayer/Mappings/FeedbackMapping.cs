using Microsoft.EntityFrameworkCore;

namespace DomainLayer.Mappings
{
    public static class FeedbackMapping
    {
        public static void Map(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Models.Feedback>()
                .Property(c => c.IsLike)
                .HasColumnType("bit")
                .IsRequired();
        }
    }
}
