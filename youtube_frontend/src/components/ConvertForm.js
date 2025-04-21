import React, { useState } from 'react'
import { AlertCircle } from 'lucide-react'

export default function ConvertForm() {
    const [url, setUrl] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)
    const [progress, setProgress] = useState(0)
    const [isPlaylist, setIsPlaylist] = useState(false) // State to toggle between video and playlist

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError(null)
        setProgress(0)

        if (!url) {
            setError('Please enter a YouTube URL')
            return
        }

        try {
            setLoading(true)
            let endpoint = isPlaylist ? '/api/convertPlaylist' : '/api/convert'

            // Start fetching the audio file or playlist zip file
            const res = await fetch(`${endpoint}?url=${encodeURIComponent(url)}`)
            if (!res.ok) throw new Error(`Server error: ${res.status}`)

            const contentLength = res.headers.get('Content-Length')
            const total = parseInt(contentLength, 10)
            const reader = res.body.getReader()

            let received = 0
            const chunks = []

            while (true) {
                const { done, value } = await reader.read()
                if (done) break

                chunks.push(value)
                received += value.length

                if (total) {
                    setProgress(Math.round((received / total) * 100))
                }
            }

            // Create a Blob from the downloaded chunks
            const blob = new Blob(chunks, { type: isPlaylist ? 'application/zip' : 'audio/mp3' })
            const downloadUrl = window.URL.createObjectURL(blob)
            const cd = res.headers.get('Content-Disposition') || ''
            const fnMatch = cd.match(/filename="?(.+)"?/)
            const filename = fnMatch?.[1] || (isPlaylist ? 'playlist.zip' : 'audio.mp3')

            // Create a download link and trigger the download
            const a = document.createElement('a')
            a.href = downloadUrl
            a.download = filename
            document.body.appendChild(a)
            a.click()
            a.remove()
            window.URL.revokeObjectURL(downloadUrl)

        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div style={{ maxWidth: 400, margin: '2rem auto', border: '1px solid #ccc', borderRadius: '8px', padding: '1rem', boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)' }}>
            <h2 style={{ marginBottom: '1rem' }}>YouTube → MP3 / Playlist</h2>
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <input
                    type="text"
                    placeholder="https://youtu.be/ABC123xyz"
                    value={url}
                    onChange={(e) => setUrl(e.target.value)}
                    style={{ padding: '0.5rem', border: '1px solid #ccc', borderRadius: '4px' }}
                />
                {error && (
                    <div style={{ color: '#c33', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <AlertCircle size={16} /> {error}
                    </div>
                )}
                {loading && (
                    <div style={{ height: '10px', background: '#eee', borderRadius: '5px', overflow: 'hidden' }}>
                        <div style={{
                            width: `${progress}%`,
                            background: '#007bff',
                            height: '100%',
                            transition: 'width 0.3s ease'
                        }}></div>
                    </div>
                )}
                <button
                    type="submit"
                    disabled={loading}
                    style={{
                        padding: '0.75rem',
                        backgroundColor: loading ? '#ccc' : '#007bff',
                        color: '#fff',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: loading ? 'not-allowed' : 'pointer',
                    }}
                >
                    {loading ? `Downloading… ${progress}%` : (isPlaylist ? 'Download Playlist' : 'Download MP3')}
                </button>
                {/* Add a toggle for playlist */}
                <button
                    type="button"
                    onClick={() => setIsPlaylist(!isPlaylist)}
                    style={{
                        padding: '0.75rem',
                        backgroundColor: '#28a745',
                        color: '#fff',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer',
                        marginTop: '1rem',
                    }}
                >
                    {isPlaylist ? 'Switch to Single Video' : 'Switch to Playlist'}
                </button>
            </form>
        </div>
    )
}
