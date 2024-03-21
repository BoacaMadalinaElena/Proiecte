namespace DomainLayer.Models
{
    public class Contact : BaseEntity
    {
        public string Name { get; set; }
        public string Subject { get; set; }
        public string Message { get; set; }
        public string Email { get; set; }
        public DateTime DateOfSend { get; set; }

        public Contact(string name, string subject, string message, string email, DateTime dateOfSend)
        {
            Name = name;
            Subject = subject;
            Message = message;
            Email = email;
            DateOfSend = dateOfSend;
        }

        public Contact() { }
    }
}
