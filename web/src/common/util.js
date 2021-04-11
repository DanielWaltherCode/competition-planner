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

function getDisplayTime(time) {
    if (time < 10) {
        time = "0" + time;
    }
    return time;
}