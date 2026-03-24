def filter_lines(input_file, output_file):
    with open(input_file, 'r') as infile, open(output_file, 'w') as outfile:
        for line in infile:
            if not (line.startswith('CONTAINER') or line.startswith('Timestamp') or line.startswith('----------')):
                outfile.write(line)

input_file = 'output_fileDocker.txt'
output_file = 'out.txt'

filter_lines(input_file, output_file)
