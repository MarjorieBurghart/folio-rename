# =============================================================================
# Folio Batch Renamer - a simple application to rename files, typically pictures, according to the fashion used for manuscript material distributed across folios, with a recto and verso Folio Batch Renamer
# 
# Folio Batch Renamer is a tool designed to help users who need to batch rename files, typically pictures, according to the fashion used for manuscript material, i.e. where the same folio number appears twice, typically followed by an indication of "recto" or "verso" side. Although rather trivial, this operation is not easily performed with traditional batch rename tools, especially for people who are not specially familiar with regular expressions or such. The tool works on files as well as folders, but always processes them one type at a time. It goes through the list of files (or folders), following the alphabetical order, and renames each according to the rules defined by the user.
# 
# DISCLAIMER This application is in beta-test mode, do not use it on important material without making a copy of your files. Generally, use at your own risk.
# 
# The Python version is the most recent (Dec. 2023). 
# 
# In the GUI, the user can define the renaming rules:
# 
# - choose the folder where the files to be renamed are;
# - if a "prefix" should be added to the new file/folder name, typically the manuscript shelfmark (e.g.: "Paris, BnF, lat. 16480, fol. ")
# - the folio number from which the automatic numbering should start, and...
# - ... whether it is a recto or a verso;
# - on how many digits this folio number should be represented (for instance, if your first image is of folio number is 99 verso, and you choose to represent the folio number with 4 digits, the first file of the list will be renamed as "0099v"
# - you can choose the suffix you want to apply for recto or verso (by default, "r" and "v" respectively);
# - finally, you can add a general suffix, that will follow the recto / verso suffix. Note that the application can run in "Test mode", showing you how the rules that will be applied to your files / folders.
# 
# Developer: Marjorie Burghart, CNRS - CIHAM UMR 5648 marjorie.burghart@cnrs.fr (with support from ChatGPT for the Python version)
# Licence GNU GPL (v3)
# 
# =============================================================================


import os
import tkinter as tk
from tkinter import ttk, filedialog

class ToolTip:
    def __init__(self, widget, text):
        self.widget = widget
        self.text = text
        self.tooltip = None
        self.widget.bind("<Enter>", self.show_tooltip)
        self.widget.bind("<Leave>", self.hide_tooltip)

    def show_tooltip(self, event):
        x, y, _, _ = self.widget.bbox("insert")
        x += self.widget.winfo_rootx() + 25
        y += self.widget.winfo_rooty() + 20

        self.tooltip = tk.Toplevel(self.widget)
        self.tooltip.wm_overrideredirect(True)
        self.tooltip.wm_geometry(f"+{x}+{y}")

        label = tk.Label(self.tooltip, text=self.text, justify="left", background="#ffffe0", relief="solid", borderwidth=1, padx=4, pady=2)
        label.pack(ipadx=1)

    def hide_tooltip(self, event):
        if self.tooltip:
            self.tooltip.destroy()
            self.tooltip = None

def batch_rename_files(folder_path, rename_config, test_mode=False):
    files = sorted(os.listdir(folder_path))

    folio_number = rename_config['start_folio']

    for idx, file_name in enumerate(files):
        old_path = os.path.join(folder_path, file_name)

        # Determine new file/folder name and extension based on configuration
        new_name, extension = generate_new_name(folio_number, idx, rename_config, file_name)
        new_path = os.path.join(folder_path, new_name + extension)

        if not test_mode:
            os.rename(old_path, new_path)

        print(f"{old_path} -> {new_path}")

        # Update folio number for the next iteration
        folio_number = update_folio_number(folio_number, idx, rename_config)

def generate_new_name(folio_number, file_index, config, file_name):
    # Increment folio number for each recto
    if file_index % 2 == 0 and file_index != 0 and config['first_fol_type'] == 'recto':
        # starts with a recto, all even idx is a recto
        folio_number += 1
    if file_index % 2 != 0 and config['first_fol_type'] == 'verso':
        #starts with a verso, all odd idx is a recto
        folio_number += 1
    
    # Generate formatted folio number
    formatted_folio = f"{folio_number:0{config['folio_digits']}d}"

    # Determine recto or verso suffix based on file index
#    side_suffix = config['recto_suffix'] if file_index % 2 == 0 else config['verso_suffix'] 
    if config['first_fol_type'] == 'recto':
        side_suffix = config['recto_suffix'] if file_index % 2 == 0 else config['verso_suffix']        
    else:
        side_suffix = config['recto_suffix'] if file_index % 2 != 0 else config['verso_suffix'] 

    # Extract the file extension (if any)
    base_name, extension = os.path.splitext(file_name)

    # Construct new name based on configuration
    new_name = f"{config['prefix']}{formatted_folio}{side_suffix}{config['general_suffix']}"

    return new_name, extension

def update_folio_number(folio_number, file_index, config):
    # Increment folio number for each recto (excluding the first file)
    if file_index % 2 == 0 and file_index != 0 and config['first_fol_type'] == 'recto':
        folio_number += 1
    if file_index % 2 != 0 and config['first_fol_type'] == 'verso':
        folio_number += 1   
    
#    if file_index % 2 == 0 and file_index != 0:
#        folio_number += 1
    return folio_number

def get_folder_path():
    folder_path = filedialog.askdirectory()
    folder_path_entry.delete(0, tk.END)
    folder_path_entry.insert(0, folder_path)

def run_batch_rename():
    folder_path = folder_path_entry.get()
    rename_config = {
        'rename_folders': True,
        'prefix': prefix_entry.get(),
        'start_folio': int(start_folio_entry.get()),
        'folio_digits': int(folio_digits_entry.get()),
        'recto_suffix': recto_suffix_entry.get(),
        'verso_suffix': verso_suffix_entry.get(),
        'first_fol_type': first_fol_type_var.get(),
        'general_suffix': general_suffix_entry.get(),
    }
    test_mode = test_mode_var.get()

    batch_rename_files(folder_path, rename_config, test_mode)

# Create GUI window
root = tk.Tk()
root.title("FolioRename Tool")

# Function to create tooltips for labels
def create_tooltip(widget, text):
    tooltip = ToolTip(widget, text)




# Paragraph of text at the top
text_paragraph = """This tool is designed to help users who need to batch rename files, typically pictures, according to the fashion used for manuscript material, i.e., where the same folio number appears twice, typically followed by an indication of "recto" or "verso" side. Although rather trivial, this operation is not easily performed with traditional batch renaming tools, especially for people who are not specially familiar with regular expressions or such.
Folio Batch Rename works on files as well as folders and renames them according to the rules defined by the user."""

paragraph_label = tk.Label(root, text=text_paragraph, wraplength=600, justify="left", padx=10, pady=10)
paragraph_label.grid(row=0, column=0, columnspan=4)




# Folder path
folder_path_label = tk.Label(root, text="Folder containing the images to rename:")
folder_path_label.grid(row=1, column=0, sticky=tk.E, padx=(15, 0))
create_tooltip(folder_path_label, "Choose the folder where the images you want to rename are.")

folder_path_entry = tk.Entry(root, width=50)
folder_path_entry.grid(row=1, column=1, padx=15, pady=5)

browse_button = tk.Button(root, text="Browse", command=get_folder_path)
browse_button.grid(row=1, column=2, padx=15, pady=5)

# Configuration entries
prefix_label = tk.Label(root, text="Prefix:")
prefix_label.grid(row=2, column=0, sticky=tk.E, padx=(15, 0))
create_tooltip(prefix_label, "The prefix is what will come before the folio number, typically your manuscript shelfmark. Example of prefix: 'Paris, BnF, lat. 16480, fol. '.")

prefix_entry = tk.Entry(root, width=60)
prefix_entry.grid(row=2, column=1, padx=15, pady=5)

start_folio_label = tk.Label(root, text="Folio number of the first image:")
start_folio_label.grid(row=3, column=0, sticky=tk.E, padx=(15, 0))  # Adjusted padx
create_tooltip(start_folio_label, "What is the number of the first folio ?")

start_folio_entry = tk.Entry(root, width=5)
start_folio_entry.grid(row=3, column=1, padx=15, pady=5)

firstFolType_label = tk.Label(root, text="is the first image of a recto or verso?:")
firstFolType_label.grid(row=3, column=2, sticky=tk.E, padx=(15, 0))
create_tooltip(firstFolType_label, "Is the first image of a recto or a verso?")

first_fol_type_var = tk.StringVar(value="recto")  # Set default value to 'recto'
first_fol_type_choices = ["recto", "verso"]
first_fol_type_dropdown = ttk.Combobox(root, textvariable=first_fol_type_var, values=first_fol_type_choices)
first_fol_type_dropdown.grid(row=3, column=3, padx=15, pady=5)  # Adjusted padx


recto_suffix_label = tk.Label(root, text="Recto Suffix:")
recto_suffix_label.grid(row=4, column=0, sticky=tk.E)
create_tooltip(recto_suffix_label, "What suffix should we add to the folio number for the recto side?")

recto_suffix_entry = tk.Entry(root, width=5)
recto_suffix_entry.grid(row=4, column=1, padx=15, pady=5)
recto_suffix_entry.insert(0, "r")  # Default value

verso_suffix_label = tk.Label(root, text="Verso Suffix:")
verso_suffix_label.grid(row=4, column=2, sticky=tk.E)
create_tooltip(verso_suffix_label, "What suffix should we add to the folio number for the verso side?")

verso_suffix_entry = tk.Entry(root, width=5)
verso_suffix_entry.grid(row=4, column=3, padx=15, pady=5)
verso_suffix_entry.insert(0, "v")  # Default value

folio_digits_label = tk.Label(root, text="Folio Digits:")
folio_digits_label.grid(row=5, column=0, sticky=tk.E)
create_tooltip(folio_digits_label, "How many digits for the folio numbers? For instance, if you choose 3, fol. 1r will be written as fol. 001r.")

folio_digits_entry = tk.Entry(root, width=2)
folio_digits_entry.grid(row=5, column=1, padx=15, pady=5)
folio_digits_entry.insert(0, "3")  # Default value

general_suffix_label = tk.Label(root, text="General Suffix:")
general_suffix_label.grid(row=5, column=2, sticky=tk.E)
create_tooltip(general_suffix_label, "You can add a general suffix, that will follow the recto / verso suffix.")

general_suffix_entry = tk.Entry(root, width=5)
general_suffix_entry.grid(row=5, column=3, padx=15, pady=5)

# Test mode checkbox
test_mode_label = tk.Label(root, text="Test Mode: ")
test_mode_label.grid(row=6, column=0, columnspan=4, sticky=tk.E+tk.W, pady=5)  # Centered label
create_tooltip(test_mode_label, "If checked, the files will not be actually renamed, but the renaming pattern will be displayed in the console.")

test_mode_var = tk.BooleanVar(value=True)  # Set default value to True
test_mode_checkbox = tk.Checkbutton(root, variable=test_mode_var)
test_mode_checkbox.grid(row=6, column=1, columnspan=3, padx=5, pady=5)  # Centered checkbox


# Run button
run_button = tk.Button(root, text="Run FolioRename", command=run_batch_rename)
run_button.grid(row=7, column=0, columnspan=4, pady=10)

# Start GUI main loop
root.mainloop()
