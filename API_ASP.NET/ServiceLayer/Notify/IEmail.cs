using ServiceLayer.DtoModels;

namespace ServiceLayer.Notify
{
    public interface IEmail
    {
        // functia de trimitere a email-ului
        public void Send(string dest, int code);

        public void SendFeedback(ContactDto record);
    }
}
