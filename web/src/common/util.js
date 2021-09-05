export function getFormattedDate(date) {
    if (date === null ||date === undefined) {return ""}
    const dateObject = new Date(date)
    const year = dateObject.getUTCFullYear();
    let month = dateObject.getUTCMonth() + 1;
    let day = dateObject.getUTCDate();

    return year + "-" + getDisplayTime(month) + "-" + getDisplayTime(day);
}

export function getHoursMinutes(date) {
    if (date === null) {return ""}
    const dateObject = new Date(date)
    return getDisplayTime(dateObject.getHours()) + ":" + getDisplayTime(dateObject.getMinutes())
}

export function getPlayerOne(match) {
    let playerOne = ""
    if (match.firstPlayer.length === 1) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName + " " + match.firstPlayer[0].club.name
    } else if (match.firstPlayer.length === 2) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName + " " + match.firstPlayer[0].club.name + "/" +
            match.firstPlayer[1].firstName + " " + match.firstPlayer[1].lastName + " " + match.firstPlayer[1].club.name
    }
    return playerOne
}

export function getPlayerTwo(match) {
    let playerTwo = ""
    if (match.secondPlayer.length === 1) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName + " " + match.secondPlayer[0].club.name
    } else if (match.firstPlayer.length === 2) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName + " " + match.secondPlayer[0].club.name + "/" +
            match.secondPlayer[1].firstName + " " + match.secondPlayer[1].lastName + " " + match.secondPlayer[1].club.name
    }
    return playerTwo
}

export function getDisplayTime(time) {
    if (time < 10) {
        time = "0" + time;
    }
    return time;
}