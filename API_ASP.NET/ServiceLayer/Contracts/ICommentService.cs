using DomainLayer.Models;
using ServiceLayer.DtoModels;

namespace ServiceLayer.Contracts
{
    public interface ICommentService
    {
        IEnumerable<CommentDto> GetByArticleId(int id);

        CommentDto? Get(int id);

        void Delete(int entityId);

        void Update(int id, CreateCommentDto entity);

        void Insert(CreateCommentDto entity);
    }
}
