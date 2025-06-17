from time import sleep, strftime
from requests import get, RequestException
from faker import Faker

fake = Faker()
success = 0
fail = 0
def call_rolldice():
    global success, fail
    name = fake.first_name()
    url = f"http://localhost:8080/rolldice?player={name}"
    try:
        response = get(url, timeout=5)
        print(f"{strftime('%Y-%m-%d %H:%M:%S')} - {name} rolled: {response.text}")
        success+=1
    except RequestException as e:
        print(f"{strftime('%Y-%m-%d %H:%M:%S')} - Error calling endpoint: {e}")
        fail+=1

if __name__ == "__main__":
    try:
        while True:
            call_rolldice()
            sleep(1)
    except KeyboardInterrupt:
        print(f"\n{success} successful requests")
        print(f"{fail} failed requests")
        s = success + fail
        print(f"{s} requests sent", end="\n")
        r = success / s * 100
        print(f"{r:.2f}% success ratio")
