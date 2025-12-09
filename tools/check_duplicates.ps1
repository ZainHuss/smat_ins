param([string]$path)
if (-not (Test-Path $path)) { Write-Error "File not found: $path"; exit 1 }
$lines = Get-Content -LiteralPath $path -Encoding UTF8
$map = @{}
for ($i=0; $i -lt $lines.Length; $i++) {
    $line = $lines[$i]
    $trim = $line.Trim()
    if ($trim -eq '') { continue }
    if ($trim.StartsWith('#') -or $trim.StartsWith('//')) { continue }
    if ($line -match '^\s*([^=]+)\s*=') {
        $k = $matches[1].Trim()
        if (-not $map.ContainsKey($k)) { $map[$k] = @() }
        $map[$k] += ($i+1)
    }
}
$dups = $map.GetEnumerator() | Where-Object { $_.Value.Count -gt 1 }
if ($dups.Count -eq 0) { Write-Output "No duplicates found"; exit 0 }
Write-Output "Duplicate keys found:"
foreach ($d in $dups) {
    Write-Output ("$($d.Key) => lines: $($d.Value -join ',')")
}
exit 0

