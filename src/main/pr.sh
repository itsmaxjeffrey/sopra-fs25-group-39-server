#!/bin/bash

# First check if 'tree' command is available
if command -v tree &> /dev/null; then
    echo "Using tree command to generate directory structure..."
    # Use tree command with options:
    # -a: Show all files (including hidden files)
    # -F: Append indicator (*/=>@|) to entries
    # --dirsfirst: List directories before files
    tree -a -F --dirsfirst . > dir.txt
    echo "Directory structure saved to dir.txt"
else
    echo "Tree command not found. Using find command as fallback..."
    
    # Function to print indented directory structure
    print_structure() {
        local dir="$1"
        local indent="$2"
        local output_file="$3"
        
        # Print the current directory name
        if [ "$dir" = "." ]; then
            echo "." >> "$output_file"
        else
            echo "${indent}${dir##*/}/" >> "$output_file"
        fi
        
        # First list all subdirectories
        find "$dir" -mindepth 1 -maxdepth 1 -type d | sort | while read -r subdir; do
            print_structure "$subdir" "$indent│   " "$output_file"
        done
        
        # Then list all files
        find "$dir" -mindepth 1 -maxdepth 1 -type f | sort | while read -r file; do
            echo "${indent}├── ${file##*/}" >> "$output_file"
        done
    }
    
    # Start with empty file
    > dir.txt
    
    # Print the structure starting from current directory
    print_structure "." "" "dir.txt"
    
    echo "Directory structure saved to dir.txt"
fi

# Display the number of files and directories
echo -e "\nStatistics:" >> dir.txt
echo "Total files: $(find . -type f | wc -l)" >> dir.txt
echo "Total directories: $(find . -type d | wc -l)" >> dir.txt
echo "Last updated: $(date)" >> dir.txt

echo "Script completed. You can view the directory structure in dir.txt"