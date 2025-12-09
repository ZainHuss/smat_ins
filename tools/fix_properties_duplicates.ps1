$path = 'C:\Users\Hussein\workspace\attach_improve\smat-ins\src\main\java\com\smat\ins\view\resources\interface_en.properties'
$lines = Get-Content -LiteralPath $path -Encoding UTF8
$last = @{}
for ($i=0; $i -lt $lines.Count; $i++) {
    $line = $lines[$i]
    if ($line -match '^\s*([^#\s][^=]*)=') {
        $k = $matches[1].Trim()
        $last[$k] = $i
    }
}
$out = New-Object System.Collections.Generic.List[string]
for ($i=0; $i -lt $lines.Count; $i++) {
    $line = $lines[$i]
    if ($line -match '^\s*([^#\s][^=]*)=') {
        $k = $matches[1].Trim()
        if ($last[$k] -eq $i) {
            $out.Add($line)
        } else {
            # comment earlier duplicate
            $out.Add('#' + $line)
        }
    } else {
        $out.Add($line)
    }
}
$out | Set-Content -LiteralPath $path -Encoding UTF8

# Check for remaining duplicates
$lines2 = Get-Content -LiteralPath $path -Encoding UTF8
$keys = @{}
for ($i=0; $i -lt $lines2.Count; $i++) {
    $line = $lines2[$i]
    if ($line -match '^\s*([^#\s][^=]*)=') {
        $k = $matches[1].Trim()
        if (-not $keys.ContainsKey($k)) { $keys[$k] = 0 }
        $keys[$k] = $keys[$k] + 1
    }
}
$dups = $keys.GetEnumerator() | Where-Object { $_.Value -gt 1 }
if ($dups) {
    Write-Output "Duplicates remain:"
    $dups | ForEach-Object { Write-Output ("$($_.Name) => $($_.Value)") }
} else {
    Write-Output "No duplicates remain."
}
Write-Output "Done."
