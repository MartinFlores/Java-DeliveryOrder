const SECRET_KEY = 'ahome-conecta-key'

export const encrypt = (data: any): string => {
  const text = JSON.stringify(data)
  return btoa(
    text
      .split('')
      .map((char, i) =>
        String.fromCharCode(char.charCodeAt(0) ^ SECRET_KEY.charCodeAt(i % SECRET_KEY.length)),
      )
      .join(''),
  )
}

export const decrypt = <T = any>(encoded: string): T | null => {
  try {
    const decoded = atob(encoded)
    const text = decoded
      .split('')
      .map((char, i) =>
        String.fromCharCode(char.charCodeAt(0) ^ SECRET_KEY.charCodeAt(i % SECRET_KEY.length)),
      )
      .join('')
    return JSON.parse(text) as T
  } catch (e) {
    console.error('Error decrypting data:', e)
    return null
  }
}
