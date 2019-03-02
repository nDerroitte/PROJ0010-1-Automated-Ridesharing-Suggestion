function [ habits ] = Sfilter(y,period)
%compute the sum of the distance between a point and its left and right
%neighbour.
%Ex: [1 2 5 8] with a period of 10 output
% [4 4 6 6]

%compute superposed signal:
raw_signal = gethabit(y,period);
%duplicate value
signal = extend(raw_signal);

s1 = signal; 
s2= circshift(s1,1);

diff = s1-s2;
diff(1) = s1(1) + period - s1(end);
diff2 = circshift(diff,length(diff)-1);
score= diff+diff2;
[value,index] = sort(score);

minPt = (length(y)/period)/3;
to_remove = mod(length(y),period);
y(end-to_remove+1 : end) = [];
y = reshape(y,period,[]);
nbcluster = mean(sum(y,1))
display(period)

%Clustering algorithm... 

filtred_index = signal(index(1:floor(0.8*length(index))));
cluster_size = value(floor(0.8*length(index)))
filtred_signal = zeros(length(raw_signal),1);
for i=1 : length(filtred_index)
    filtred_signal(filtred_index(i)) = filtred_signal(filtred_index(i)) + 1;
end
figure
plot(filtred_signal)
title(strcat('filtred signal period: ', num2str(period)))

%Compute intracluster variance:
%...
    



end

