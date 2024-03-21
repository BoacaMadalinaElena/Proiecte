using DomainLayer.Models;
using RepositoryLayer;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;

namespace ServiceLayer.Services
{
    public class FeedbackService : IFeedbackService
    {
        // Accesare nivel inferior
        private readonly IRepository<Feedback> _feedbackRepository;

        public FeedbackService(IRepository<Feedback> feedbackRepository)
        {
            _feedbackRepository = feedbackRepository;
        }

        public FeedbackDto Get(FeedbackDto entity)
        {
            var feed = _feedbackRepository.GetAll();
            var elem = feed.FirstOrDefault(u => u.CommentId == entity.CommentId && u.UserId == entity.UserId);

            if (elem == null)
            {
                return null;
            }

            return new FeedbackDto(elem.CommentId, elem.UserId, elem.IsLike);
        }

        public void Delete(FeedbackDto entity)
        {
            var feed = _feedbackRepository.GetAll();
            var elem = feed.FirstOrDefault(u => u.CommentId == entity.CommentId && u.UserId == entity.UserId);
            {
                if (elem != null)
                    _feedbackRepository.Delete(elem.Id);
            }
        }

        public void Insert(FeedbackDto entity)
        {
            var feedbacks = _feedbackRepository.GetAll();
            var elem = feedbacks.FirstOrDefault(u => u.CommentId == entity.CommentId && u.UserId == entity.UserId);
            if (elem == null)
            {
                var feed = new Feedback(entity.CommentId, entity.UserId, entity.IsLike);
                _feedbackRepository.Insert(feed);
            }else if(elem.IsLike != entity.IsLike){
                _feedbackRepository.Delete(elem.Id);
                var feed = new Feedback(entity.CommentId, entity.UserId, entity.IsLike);
                _feedbackRepository.Insert(feed);
            }
        }

        public void Update( FeedbackDto entity)
        {

            var feedbacks = _feedbackRepository.GetAll();
            var existingFeed = feedbacks.FirstOrDefault(u => u.CommentId == entity.CommentId && u.UserId == entity.UserId);

            if (existingFeed != null)
            {
                existingFeed.CommentId = entity.CommentId;
                existingFeed.UserId = entity.UserId;
                existingFeed.IsLike = entity.IsLike;

                _feedbackRepository.SaveChanges();
            }
            else
            {
                throw new Exception("Entity is null");
            }
        }

        public IEnumerable<FeedbackDto> GetByArticleId(int id)
        {
            var feed = _feedbackRepository.GetAll()
                .Where(u => u.CommentId == id)
                .Select(u => new FeedbackDto(
                    u.CommentId,
                    u.UserId,
                    u.IsLike
                ));

            return feed.ToList();
        }

        public IEnumerable<FeedbackDto> GetByCommentId(int id)
        {
            var elements = _feedbackRepository.GetAll();
            var results = elements.
                Where(e => e.CommentId == id).
                Select(e => new FeedbackDto(e.CommentId,e.UserId,e.IsLike));
            return results;
        }
    }
}
