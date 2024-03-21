namespace ServiceLayer.DtoModels
{
    public class FilterSortConfigDto
    {
        public string Search { set; get; }
        public ICollection<string> OptionsCategory { set; get; }
        public ICollection<string> OptionsCity { set; get; }
        public ICollection<string> OptionsAuthor { set; get; }
        public string OptionsEventDate { set; get; }
        public string OptionsPostDate { set; get; }
        public string OptionsSortedBy { set; get; }

        public FilterSortConfigDto()
        {
            // Constructor fără parametri
        }

        public FilterSortConfigDto(string search,ICollection<string> optionsCategory, ICollection<string> optionsCity, ICollection<string> optionsAuthor, string optionsEventDate, string optionsPostDate, string optionsSortedBy)
        {
            Search = search;
            this.OptionsCategory = optionsCategory;
            this.OptionsCity = optionsCity;
            this.OptionsAuthor = optionsAuthor;
            this.OptionsEventDate = optionsEventDate;
            this.OptionsPostDate = optionsPostDate;
            this.OptionsSortedBy = optionsSortedBy;
        }
    }
}
