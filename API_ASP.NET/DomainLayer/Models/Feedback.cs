namespace DomainLayer.Models
{
    public class Feedback : BaseEntity
    {
        public int CommentId { get; set; }

        // Sau user dar apar dependente circulare
        public int UserId { get; set; }
        public bool IsLike { get; set; }
        public Comment Comment { get; set; }
        public Feedback(int commentId, int userId, bool isLike)
        {
            CommentId = commentId;
            UserId = userId;
            IsLike = isLike;
        }
    }
}
