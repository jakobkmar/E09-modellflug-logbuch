function zeroPadNumber(num: number): string {
  return num < 10 ? `0${num}` : `${num}`
}

function simpleDateString(date: Date): string {
  return `${date.getFullYear()}-${zeroPadNumber(date.getMonth() + 1)}-${zeroPadNumber(date.getDate())}`
}

export function getDateStringToday(): string {
  const date = new Date()
  return simpleDateString(date)
}

export function getDateStringYesterday(): string {
  const date = new Date()
  date.setDate(date.getDate() - 1)
  return simpleDateString(date)
}

export function getDateStringWeekAgo(): string {
  const date = new Date()
  date.setDate(date.getDate() - 7)
  return simpleDateString(date)
}

export function getDateStringMonthAgo(): string {
  const date = new Date()
  date.setMonth(date.getMonth() - 1)
  return simpleDateString(date)
}

export function getTimeString(): string {
  const date = new Date()
  return `${zeroPadNumber(date.getHours())}:${zeroPadNumber(date.getMinutes())}`
}
