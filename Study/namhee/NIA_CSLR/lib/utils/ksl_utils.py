from itertools import groupby

def clean_ksl(prediction):
    prediction = prediction.strip()
    # prediction = re.sub(r" +", " ", prediction)
    
    # Remove white spaces and repetitions
    prediction = " ".join(
        " ".join(i[0] for i in groupby(prediction.split(" "))).split()
    )
    prediction = prediction.strip()

    return prediction