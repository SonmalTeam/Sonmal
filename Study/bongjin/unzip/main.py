# SuperFastPython.com
# unzip a large number of files concurrently with processes in batch
import glob
from zipfile import ZipFile
from concurrent.futures import ProcessPoolExecutor

pathaa='/home/jupyter-j7d202/newDataset/video/'

# unzip files from an archive
def unzip_files(zip_filename, filenames, path):
    # open the zip file
    print(zip_filename)
    with ZipFile(zip_filename, 'r') as handle:
        # unzip multiple files
        for filename in filenames:
            # unzip the file
            handle.extract(filename, path)
            # report progress
            print(f'.unzipped {filename}')


# unzip a large number of files
def main(path='/home/jupyter-j7d202/newDataset/video/', zip_filename='testing.zip'):
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
    files = glob.glob(pathaa+'*.zip')
    for fileName in files:
        main(pathaa, fileName)
