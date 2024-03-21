using Microsoft.IdentityModel.Tokens;
using System.Collections;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;

namespace ServiceLayer.SessionVariable
{
    public class ManagementToken
    {
        private static string _SecretKey = null;


        /// <summary>
        /// Metoda returneaza un nou token
        /// </summary>
        /// <returns></returns>
        public string GetToken(int id)
        {
            if (_SecretKey == null)
            {
                // Cheia secreta folosita pentru semnarea token-ului
                byte[] secretKeyBytes = new byte[16]; // 128 de biți = 16 octeți
                using (var rng = new System.Security.Cryptography.RNGCryptoServiceProvider())
                {
                    rng.GetBytes(secretKeyBytes);
                }

                // Convertire tabloul de octeti intr-un sir codificat în Base64 pentru utilizarea cu JWT
                _SecretKey = Convert.ToBase64String(secretKeyBytes);
            }

            // Data actuala
            var now = DateTime.UtcNow;

            // Data de expirare: adaugam o lună la data actuala
            var expirationDate = now.AddMonths(1);

            // Claims-uri pentru token 
            var claims = new[] {
        new Claim("userId", id.ToString())
    };

            var token = new JwtSecurityToken(
                claims: claims,
                expires: expirationDate,
                signingCredentials: new SigningCredentials(new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_SecretKey)), SecurityAlgorithms.HmacSha256)
            );

            var tokenString = new JwtSecurityTokenHandler().WriteToken(token);

            return tokenString;
        }


        public int? IsValid(string token)
        {
            try
            {
                var tokenHandler = new JwtSecurityTokenHandler();
                var key = Encoding.UTF8.GetBytes(_SecretKey);

                var validationParameters = new TokenValidationParameters
                {
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = new SymmetricSecurityKey(key),
                    ValidateIssuer = false,
                    ValidateAudience = false
                };

                var principal = tokenHandler.ValidateToken(token, validationParameters, out var validatedToken);

                var claims = principal.Claims;

                var userIdClaim = claims.FirstOrDefault(c => c.Type == "userId");
                if (userIdClaim != null && int.TryParse(userIdClaim.Value, out var userId))
                {
                    return userId;
                }
            }
            catch (System.ArgumentNullException ex)
            {
                throw new Exception();
            }
            catch (Exception ex)
            {
                // Token-ul nu este valid sau a apărut o eroare la validare
                throw ex;
            }

            return null;
        }
    }
}

