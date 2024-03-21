import os

def count_non_empty_lines_in_java_files(directory):
    total_lines = 0

    for root, dirs, files in os.walk(directory):
        # Exclude directories with the parent directory named "target"
        if "target" in dirs:
            dirs.remove("target")
            continue


        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    lines = f.readlines()
                    non_empty_lines = [line.strip() for line in lines if line.strip()]
                    total_lines += len(non_empty_lines)

    return total_lines

if __name__ == "__main__":
    current_directory = os.getcwd()
    total_java_lines = count_non_empty_lines_in_java_files(current_directory)

    print(f"Numarul total de linii non-goale în fisierele .java din subdirectoarele curente: {total_java_lines}")

