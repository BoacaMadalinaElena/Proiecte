namespace ServiceLayer.DtoModels
{
    public class ContactDto
    {
        public string Name { get; set; }
        public string Subject { get; set; }
        public string Message { get; set; }
        public string Email { get; set; }
        public DateTime? DateOfSend { get; set; }

        public ContactDto(string name, string email, string subject, string message,DateTime? dateOfSend)
        {
            Name = name;
            Subject = subject;
            Message = message;
            Email = email;
            if(dateOfSend == null)
            {
                DateOfSend = DateTime.Now;
            }
            else
            {
                DateOfSend = dateOfSend;
            }
        }


        public string StringToSend()
        {
            return "Dear Admin,\n\nYou have received a feedback from " + Name + " (" + Email + ") on " + DateOfSend + ".\n\nSubject: " + Subject + " \r\nMessage: " + Message + "\n\nBest regards,\nYour InnerGlow Team";
        }

        public ContactDto()
        {

        }
    }
}
