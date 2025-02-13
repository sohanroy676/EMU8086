;Connections
connect 1, 01

;Memory
mov [30], 20
mov [31], 10
mov [32], 08
mov [33], 04
mov [34], 02
mov [35], 01
mov [36], 0f9
mov [37], 30
mov [38], 0c3
mov [39], 0e1
mov [3a], 86
mov [3b], 0c0

;Main code
mov dx, 0ff36
mov al, 80
out dx, al
mov ah, 6 ;ADDR (1) 22
mov di, 30
mov al, [di] ;ADDR (2) 24
mov dx, 0ff30
out dx, al
mov al, [di + 6]
mov dx, 0ff32
out dx, al
inc di
call 37
dec ah
jnz 24
jmp 22

;Delay code
mov dl, 0f ;ADDR (5) 37
mov cx, 0ffff ;ADDR (4) 38
loop 39 ;ADDR (3) 39
dec dl
jnz 38
ret