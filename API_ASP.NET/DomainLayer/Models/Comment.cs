using DomainLayer;

namespace DomainLayer.Models
{
    public class Comment : BaseEntity
    {
        // Ar trebui un User nu un int la legatura dar am referinte circulare
        public int UserId { get; set; }
        public string Content { get; set; }
        public DateTime CreatedAt { get; set; }

        public int ArticleId { get; set; }
        public int Article { get; set; }

        // Nu orice comentariu are feedback
        public ICollection<Feedback>? Feedbacks { get; set; }

        public Comment(int userId, int articleId,string content, DateTime createdAt)
        {
            ArticleId = articleId;
            UserId = userId;
            Content = content;
            CreatedAt = createdAt;
        }
    }
}
