using System.Runtime.InteropServices;
using DomainLayer.Mappings;
using DomainLayer.Models;
using Microsoft.EntityFrameworkCore;

namespace DomainLayer.Context
{
    public class BlogDbContext : DbContext
    {
        // String-uri de conexiune
        private readonly string WindowsConnectionString = @"Server=.\SQLExpress;Database=InnerGlowSP23DB;Trusted_Connection=True;TrustServerCertificate=true";
        private readonly string Windows2ConnectionString = @"Server=localhost\SQLEXPRESS;Database=InnerGlowSP23DB;Trusted_Connection=True;TrustServerCertificate=True;";
        private readonly string MacOSConnectionString = "Server=localhost,1433;Database=InnerGlowSP23DB;User=SA;Password=Admin123;TrustServerCertificate=True;Encrypt=false;";

        public BlogDbContext() { }

        public BlogDbContext(DbContextOptions<BlogDbContext> options) : base(options) { }

        protected override void OnModelCreating(ModelBuilder builder)
        {
            base.OnModelCreating(builder);

            // Relatii intre tabele Feedback si Comment
            builder.Entity<Feedback>()
                .HasOne(f => f.Comment)
                .WithMany(c => c.Feedbacks)
                .HasForeignKey(f => f.CommentId);

            builder.Entity<Article>()
                .HasMany(a => a.ArticleImages)   
                .WithOne(ai => ai.Article)       
                .HasForeignKey(ai => ai.ArticleId);

            builder.Entity<ArticleImage>()
                .HasKey(ai => ai.Id);

            // Constrangeri pe fiecare tabela
            ArticleImageMapping.Map(builder);
            ArticleMapping.Map(builder);
            CategoryMapping.Map(builder);
            CommentMapping.Map(builder);
            ContactMapping.Map(builder);
            FeedbackMapping.Map(builder);
            UserMapping.Map(builder);
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            var isWindows = RuntimeInformation.IsOSPlatform(OSPlatform.Windows);
            optionsBuilder.UseSqlServer(isWindows ? WindowsConnectionString : MacOSConnectionString);
        }

        // Tabelele
        public DbSet<User> Users { get; set; }
        public DbSet<Article> Articles { get; set; }
        public DbSet<ArticleImage> ArticleImages { get; set; }
        //Categories *** 
        public DbSet<Category> Categoryes { get; set; }
        public DbSet<Comment> Comments { get; set; }
        public DbSet<Contact> Contacts { get; set; }
        public DbSet<Feedback> Feedbacks { get; set; }
    }
}
