namespace ServiceLayer.DtoModels
{
    public class CommentDto
    {
        public int Id { get; set; }
        public int UserId { get; set; }
        public string Content { get; set; }
        public DateTime CreatedAt { get; set; }
        public int ArticleId { get; set; } 
        public string AuthorComment { get; set; }
        public ICollection<FeedbackDto>? Feedbacks { get; set; }

        public CommentDto(int id,int userId,int articleId,string author, string content, DateTime createdAt, IEnumerable<FeedbackDto> feed)
        {
            Id = id;
            UserId = userId;
            Content = content;
            CreatedAt = createdAt;
            Feedbacks = feed == null ? null : feed.ToList();
            ArticleId = articleId;
            AuthorComment = author;
        }

        public CommentDto()
        {
        }

        public CommentDto(int id, int userId, string content, DateTime createdAt, int articleId, string author, ICollection<FeedbackDto>? feedbacks)
        {
            Id = id;
            UserId = userId;
            Content = content;
            CreatedAt = createdAt;
            ArticleId = articleId;
            Feedbacks = feedbacks;
        }
    }
}
