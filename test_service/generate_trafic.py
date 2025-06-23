from time import sleep, strftime
from requests import get, RequestException
from faker import Faker
from sys import argv
from random import randint

fake = Faker()
success = 0
fail = 0
timeout = 5
def call_rolldice():
    global success, fail, timeout
    name = fake.first_name()

    # randomly choose a request type
    request = randint(1,3)
    # request = 2 # Force a reauest to be rollDice20 to test nested spans
    kind = ""
    if request == 1:
        url = f"http://localhost:8080/rollDice?player={name}"
        kind = "6"
    elif request == 2:
        url = f"http://localhost:8080/rollDice20?player={name}"
        kind = "20"
    else:
        faces = randint(2, 100)
        url = f"http://localhost:8080/rollDiceCustom?maximum={faces}&player={name}"
        kind = f"custom ({faces})"

    try:
        response = get(url, timeout=timeout)
        print(f"{strftime('%Y-%m-%d %H:%M:%S')} - {name} rolled {kind}: {response.text}")
        success+=1
    except RequestException as e:
        print(f"{strftime('%Y-%m-%d %H:%M:%S')} - Error calling endpoint: {e}")
        fail+=1

if __name__ == "__main__":
    seconds = int(argv[1]) if len(argv) > 1 else 1
    timeout += seconds
    print("A request will be sent every {} seconds".format(seconds))
    try:
        while True:
            call_rolldice()
            sleep(seconds)
    except KeyboardInterrupt:
        print(f"\n{success} successful requests")
        print(f"{fail} failed requests")
        s = success + fail
        print(f"{s} requests sent", end="\n")
        r = success / s * 100
        print(f"{r:.2f}% success ratio")
