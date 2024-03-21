using System.Text.Json.Serialization;
namespace ServiceLayer.DtoModels;

public class CreateCommentDto
{
    public int Id { get; set; }
    public int UserId { get; set; }
    public string Content { get; set; }
    [JsonIgnore]
    public DateTime CreatedAt { get; set; }
    public int ArticleId { get; set; }
    public CreateCommentDto(int id, int userId, int articleId, string content, DateTime createdAt)
    {
        Id = id;
        UserId = userId;
        Content = content;
        CreatedAt = createdAt;
        ArticleId = articleId;
    }

    public CreateCommentDto()
    {
    }
}

