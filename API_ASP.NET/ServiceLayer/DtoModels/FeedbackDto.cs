namespace ServiceLayer.DtoModels
{
    public class FeedbackDto
    {
        public int CommentId { get; set; }
        // Sau user
        public int UserId { get; set; }
        public bool IsLike { get; set; }
        public FeedbackDto(int commentId, int userId, bool isLike)
        {
            CommentId = commentId;
            UserId = userId;
            IsLike = isLike;
        }
    }
}
