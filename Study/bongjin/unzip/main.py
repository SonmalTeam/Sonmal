# unzip a large number of files concurrently with processes and threads in batch
import glob
from os import makedirs
from os.path import join
from zipfile import ZipFile
from concurrent.futures import ProcessPoolExecutor
from concurrent.futures import ThreadPoolExecutor


# save file to disk
def save_file(data, filename, path):
    # create a path
    filepath = join(path, filename)
    # write to disk
    with open(filepath, 'wb') as file:
        file.write(data)
    # report progress
    print(f'.unzipped {filename}', flush=True)


# unzip files from an archive
def unzip_files(zip_filename, filenames, path):
    # open the zip file
    with ZipFile(zip_filename, 'r') as handle:
        # create a thread pool
        with ThreadPoolExecutor(20) as exe:
            # unzip each file
            for filename in filenames:
                # decompress data
                data = handle.read(filename)
                # save to disk
                _ = exe.submit(save_file, data, filename, path)


# unzip a large number of files
def main(path='video', zip_filename='testing.zip'):

    zipFiles = glob.glob('/home/jupyter-j7d202/newDataset/*.zip')

    for zip_filename in zipFiles:
    # open the zip file
    with ZipFile(zip_filename, 'r') as handle:
        # list of all files to unzip
        files = handle.namelist()
    # determine chunksize
    n_workers = 40
    chunksize = round(len(files) / n_workers)
    # start the thread pool
    with ProcessPoolExecutor(n_workers) as exe:
        # split the copy operations into chunks
        for i in range(0, len(files), chunksize):
            # select a chunk of filenames
            filenames = files[i:(i + chunksize)]
            # submit the batch copy task
            _ = exe.submit(unzip_files, zip_filename, filenames, path)


# entry point
if __name__ == '__main__':
    main()