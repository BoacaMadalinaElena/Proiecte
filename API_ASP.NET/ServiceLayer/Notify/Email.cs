using MailKit.Security;
using MimeKit;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;
using ServiceLayer.Services;

namespace ServiceLayer.Notify
{
    public class Email : IEmail
    {
        public Email() { }
    

        // functia de trimitere a email-ului
        public void Send(string dest,int code) {
            string senderEmail = "innerglow045@gmail.com";
            string senderPassword = "ixruxmbokmsxnlra";
            string recipientEmail = dest; // Adresa de email a destinatarului
            string subject = "Account Recovery InnerGlow";
            string body = $"Hello,\n\nYou have requested an account recovery for your InnerGlow account.\n\nPlease use the following recovery code to proceed:\n\nCode: {code}\n\nIf you didn't request this recovery, please ignore this email.\n\nBest regards,\nThe InnerGlow Team";

            // creare obiect pentru comunicare
            var message = new MimeMessage();
            message.From.Add(new MailboxAddress("innerglow045@gmail.com",senderEmail));
            message.To.Add(new MailboxAddress("innerglow045@gmail.com", recipientEmail));
            message.Subject = subject;

            // creare email efectiv
            var bodyBuilder = new BodyBuilder();
            bodyBuilder.TextBody = body;
            message.Body = bodyBuilder.ToMessageBody();

            try
            {
                // Conectare la serverul SMTP de la Gmail
                using (var client = new MailKit.Net.Smtp.SmtpClient())
                {
                    client.Connect("smtp.gmail.com", 587, SecureSocketOptions.StartTls);

                    client.Authenticate(senderEmail, senderPassword);

                    client.Send(message);

                    client.Disconnect(true);
                }
            }
            catch (System.Net.Sockets.SocketException ex)
            {
                throw ex;
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }

        public void SendFeedback(ContactDto record)
        {
            string senderEmail = "innerglow045@gmail.com";
            string senderPassword = "ixruxmbokmsxnlra";
            string recipientEmail = "mateirares44@gmail.com";
            string subject = "Feedback InnerGlow";

            var message = new MimeMessage();
            message.From.Add(new MailboxAddress("innerglow045@gmail.com", senderEmail));
            message.To.Add(new MailboxAddress("innerglow045@gmail.com", recipientEmail));
            message.Subject = subject;

            var bodyBuilder = new BodyBuilder();
            bodyBuilder.TextBody = record.StringToSend();
            message.Body = bodyBuilder.ToMessageBody();

            try
            {
                using (var client = new MailKit.Net.Smtp.SmtpClient())
                {
                    client.Connect("smtp.gmail.com", 587, SecureSocketOptions.StartTls);

                    client.Authenticate(senderEmail, senderPassword);

                    client.Send(message);

                    client.Disconnect(true);
                }
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }


    }
}
