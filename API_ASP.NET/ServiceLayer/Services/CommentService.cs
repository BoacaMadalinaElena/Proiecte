using DomainLayer.Models;
using RepositoryLayer;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;

namespace ServiceLayer.Services
{
    public class CommentService : ICommentService
    {
        private readonly IRepository<Comment> _commentRepository;
        private readonly IRepository<User> _userRepository;

        public CommentService(IRepository<Comment> commentRepository, IRepository<User> userRepository)
        {
            _commentRepository = commentRepository;
            _userRepository = userRepository;
        }

        public CommentDto Get(int id)
        {
            var comm = _commentRepository.Get(id);

            if (comm == null)
            {
                return null;
            }

            var comments = _commentRepository.GetAll().ToList();
            var users = _userRepository.GetAll().ToList();

            var commentsJoin = comments.Join(
                         users,
                         comment => comment.UserId,
                         user => user.Id,
                         (comment, user) => new
                         {
                             Comment = comment,
                             AuthorName = user.Name,
                             AuthorId = user.Id
                         }
                     )
                     .Select(c =>
                     {
                         var feedbacks = c.Comment.Feedbacks != null
                             ? c.Comment.Feedbacks.Select(f => new FeedbackDto(f.CommentId, f.UserId, f.IsLike)).ToList()
                             : new List<FeedbackDto>();

                         return new CommentDto(c.Comment.Id, c.Comment.UserId, c.Comment.ArticleId, c.AuthorName, c.Comment.Content, c.Comment.CreatedAt, feedbacks);
                     })
                     .ToList();
            return commentsJoin[0];
        }

        public void Delete(int entityId)
        {
            _commentRepository.Delete(entityId);
        }
        public void Insert(CreateCommentDto entity)
        {
            var user = new Comment(entity.UserId, entity.ArticleId, entity.Content, entity.CreatedAt);
            _commentRepository.Insert(user);
        }
        public void Update(int id, CreateCommentDto entity)
        {
            var existingComment = _commentRepository.Get(id);
            if (existingComment == null) { throw new Exception("Entity is null"); }
            existingComment.UserId = entity.UserId;
            existingComment.ArticleId = entity.ArticleId;
            existingComment.Content = entity.Content;
            existingComment.CreatedAt = entity.CreatedAt;
            existingComment.Content = entity.Content;

            _commentRepository.SaveChanges();
        }

        public IEnumerable<CommentDto> GetByArticleId(int id)
        {
            var comments = _commentRepository.GetAll()
                .Where(u => u.ArticleId == id)
                .Join(
                    _userRepository.GetAll(),
                    comment => comment.UserId,
                    user => user.Id,
                    (comment, user) => new
                    {
                        Comment = comment,
                        AuthorName = user.Name
                    }
                )
                .Select(c =>
                {
                    var feedbacks = c.Comment.Feedbacks != null
                        ? c.Comment.Feedbacks.Select(f => new FeedbackDto(f.CommentId, f.UserId, f.IsLike)).ToList()
                        : new List<FeedbackDto>();

                    return new CommentDto(c.Comment.Id, c.Comment.UserId, c.Comment.ArticleId, c.AuthorName, c.Comment.Content, c.Comment.CreatedAt, feedbacks);
                })
                .ToList();

            return comments.ToList();
        }

    }
}
