export function getDateToday() {
  const date = new Date()
  return date.toISOString().split('T')[0]
}

export function getDateYesterday() {
  const date = new Date()
  date.setDate(date.getDate() - 1)
  return date.toISOString().split('T')[0]
}

function fillTimeInt(num: number) {
  return num < 10 ? `0${num}` : num
}

export function getTimeString() {
  const date = new Date()
  return `${fillTimeInt(date.getHours())}:${fillTimeInt(date.getMinutes())}`
}
