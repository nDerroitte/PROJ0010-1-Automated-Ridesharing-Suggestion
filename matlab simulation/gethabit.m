function habit = gethabit( y,period )
padding = mod(length(y),period);
y(end-padding+1 : end) = [];
y = reshape(y,period,[]);
habit = sum(y,2);
end

