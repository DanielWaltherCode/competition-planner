export function getFormattedDate(date) {
    function getDisplayTime(time) {
        if (time < 10) {
            time = "0" + time;
        }
        return time;
    }

    const year = date.getUTCFullYear();
    let month = date.getUTCMonth() + 1;
    let day = date.getUTCDate();

    return year + "-" + getDisplayTime(month) + "-" + getDisplayTime(day);
}