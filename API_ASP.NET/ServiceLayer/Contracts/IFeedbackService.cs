using DomainLayer.Models;
using ServiceLayer.DtoModels;

namespace ServiceLayer.Contracts
{
    public interface IFeedbackService
    {
        IEnumerable<FeedbackDto> GetByCommentId(int id);

        FeedbackDto? Get(FeedbackDto entity);

        void Delete(FeedbackDto entity);

        void Update(FeedbackDto entity);

        void Insert(FeedbackDto entity);
    }
}
